package com.socialcomputing.utils.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;

class MultipartFormDataParser {
	private String				boundary;
	private InputStream			bufInput;
	private byte[]				bufBytes;
	private int					bufPos, bufLen, bufLeft;
	private File				tmpDir;
	private static long			tmpFileNumber = System.currentTimeMillis();

	MultipartFormDataParser(String contentType, InputStream contentStream, int contentLength, File temporaryDir) throws IOException {
		Hashtable				attrs;

		// get boundary
		attrs = getMIMEHeaderAttributes(contentType);
		boundary = (String) attrs.get("boundary");
		if(boundary == null)
			throw new IOException("could not find multipart/form-data boundary");
		boundary = "--" + boundary;

		// set input buffer
		bufInput = contentStream;
		bufPos = 0;
		bufLen = 0;
		bufLeft = contentLength;
		bufBytes = new byte[4096];

		// set temporary directory to use
		tmpDir = temporaryDir;
	}

	Hashtable parse() throws IOException {
		int				i;
		String			line, linelc;
		String			fieldName, contentType, contentFilename;
		Hashtable		params, finalParams, attrs;
		Object			fieldValue;
		Object[]		fieldValues, newVals;
		int 		    expandedContentLength = -1;

		params = new Hashtable();
		try {
			// loop over fields
			FIELD_LOOP: for(;;) {
				// no boundary -> quit
				line = readLine(true);
				if(line == null || !line.equals(boundary))
					break FIELD_LOOP;

				// read field headers
				fieldName = null;
				contentType = null;
				contentFilename = null;
				for(;;) {
					line = readLine(true);
					if(line == null) {
						break FIELD_LOOP;
					} else if(line.length() == 0) {
						break;
					} else {
						linelc = line.toLowerCase();
						if(linelc.startsWith("content-disposition:") &&
								getMIMEHeaderPrimaryValue(linelc.substring(20)).equals("form-data"))
						{
							attrs = getMIMEHeaderAttributes(line);
							fieldName = (String) attrs.get("name");
							contentFilename = (String) attrs.get("filename");
						} else if(linelc.startsWith("content-type:")) {
							contentType = line.substring(13).trim();
						} else if(linelc.startsWith("original-length:")) {
							expandedContentLength = new Integer( line.substring(16).trim()).intValue();
						}
					}
				}

				// read string or file value
				fieldValue = null;
				if(fieldName == null) {
					readUntilBoundary(new ByteArrayOutputStream());
				} else if(contentFilename == null) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					if(readUntilBoundary(out) != -1)
						fieldValue = out.toString();
				} else {
					if(tmpDir == null) {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						i = readUntilBoundary(out);
						if(i != -1)
							fieldValue = new UploadedFile(contentType, i, expandedContentLength, contentFilename, out.toByteArray());
							expandedContentLength = -1;
					} else {
						File savedFile = getTmpFile(tmpDir);
						FileOutputStream out = new FileOutputStream(savedFile);
						try {
							i = readUntilBoundary(out);
							if(i != -1)
								fieldValue = new UploadedFile(contentType, i, expandedContentLength, contentFilename, savedFile);
							expandedContentLength = -1;
						} finally {
							out.close();
							if(fieldValue == null)
								savedFile.delete();
						}
					}
				}

				// add value to hashtable
				if(fieldName != null && fieldValue != null) {
					fieldValues = (Object[]) params.get(fieldName);
					if(fieldValues == null) {
						fieldValues = new Object[1];
					} else {
						newVals = new Object[fieldValues.length+1];
						for(i = 0; i < fieldValues.length; i++) {
							newVals[i] = fieldValues[i];
						}
						fieldValues = newVals;
					}
					fieldValues[fieldValues.length-1] = fieldValue;
					params.put(fieldName, fieldValues);
				}
			}
			finalParams = params;
			params = null;
		} finally {
			bufInput = null;
			bufBytes = null;
			if(params != null)
				deleteTemporaryFiles(params);
		}
		return finalParams;
	}

	/**
		Read the first 255 characters of the current line on the servlet input stream

		@param proceedToNextLine is true when the current line should be set
		to the next one

		@returns the line read or <TT>null<TT> if there is no more input
	*/
	private String readLine(boolean proceedToNextLine) throws IOException {
		int				i = 0;
		String			res;

		if(bufLen - bufPos < 256) {
			System.arraycopy(bufBytes, bufPos, bufBytes, 0, bufLen - bufPos);
			bufLen -= bufPos;
			bufPos = 0;
			fillBuffer();
		}

		if(bufPos == bufLen)
			return null;

		for(i = bufPos; i < bufPos + 255 && i < bufLen; i++) {
			if(bufBytes[i] == '\r' && bufBytes[i+1] == '\n') {
				res = new String(bufBytes, bufPos, i - bufPos);
				if(proceedToNextLine)
					bufPos = i+2;
				return res;
			}
		}

		res = new String(bufBytes, bufPos, i - bufPos);
		if(proceedToNextLine) {
			boolean justSawCR = false;

			bufPos = i;
			for(;;) {
				if(bufPos >= bufLen) {
					bufPos = 0;
					bufLen = 0;
					if(!fillBuffer())
						break;
				}

				if(bufBytes[bufPos] == '\r') {
					justSawCR = true;
				} else if(bufBytes[bufPos] == '\n' && justSawCR) {
					bufPos++;
					break;
				} else {
					justSawCR = false;
				}
				bufPos++;
			}
		}
		return res;
	}

	private int readUntilBoundary(OutputStream out) throws IOException {
		boolean		first = true;
		String		line;
		int			boundaryLen, lineLen, totalLength = 0;

		boundaryLen = boundary.length();
		for(;;) {
			line = readLine(false);
			if(line == null)
				return -1;

			if(line.startsWith(boundary)) {
				lineLen = line.length();
				if(lineLen == boundaryLen ||
					(lineLen == boundaryLen + 2 && line.charAt(boundaryLen) == '-' && line.charAt(boundaryLen+1) == '-')) {
					return totalLength;
				}
			}

			if(first) {
				first = false;
			} else {
				out.write((char) '\r');
				out.write((char) '\n');
				totalLength += 2;
			}
			totalLength += readLineFully(out);
		}
	}

	private int readLineFully(OutputStream out) throws IOException {
		boolean	justSawCR = false;
		int		lastBufPos = bufPos, totalLength = 0;

		for(;;) {
			if(bufPos >= bufLen) {
				if(lastBufPos < bufPos) {
					out.write(bufBytes, lastBufPos, bufPos - lastBufPos);
					totalLength += bufPos - lastBufPos;
				}
				lastBufPos = 0;
				bufPos = 0;
				bufLen = 0;
				if(!fillBuffer())
					break;
			}

			if(bufBytes[bufPos] == '\r') {
				justSawCR = true;
			} else if(bufBytes[bufPos] == '\n' && justSawCR) {
				if(lastBufPos < bufPos-1) {
					out.write(bufBytes, lastBufPos, (bufPos-1) - lastBufPos);
					totalLength += (bufPos-1) - lastBufPos;
				}
				bufPos++;
				break;
			} else {
				justSawCR = false;
			}
			bufPos++;
		}
		return totalLength;
	}

	private boolean fillBuffer() throws IOException {
		int			nrRead;
		boolean		readSomething = false;

		while(bufLeft > 0 && bufLen < bufBytes.length) {
			nrRead = bufInput.read(bufBytes, bufLen, Math.min(bufBytes.length - bufLen, bufLeft));
			if(nrRead >= 0) {
				bufLen += nrRead;
				bufLeft -= nrRead;
				readSomething = true;
			}
		}
		return readSomething;
	}

	private static synchronized File getTmpFile(File tmpDir) {
		File		tmpFile;

		for(;;) {
			tmpFileNumber++;
			tmpFile = new File(tmpDir, "upl" + tmpFileNumber);
			if(!tmpFile.exists())
				return tmpFile;
		}
	}

	static String getMIMEHeaderPrimaryValue(String val) {
		int		pos;

		pos = val.indexOf(';');
		if(pos < 0) {
			return val.trim();
		} else {
			return val.substring(0, pos).trim();
		}
	}

	static Hashtable getMIMEHeaderAttributes(String val) {
		int					pos, pos2;
		StringTokenizer 	toker;
		String				tok, k, v;
		Hashtable			attrs = new Hashtable();

// FIXME: this is incorrect: what if there is a semicolon in the name or filename?
		toker = new StringTokenizer(val, ";");
		while(toker.hasMoreTokens()) {
			tok = toker.nextToken();
			pos = tok.indexOf('=');
			if(pos > 0 && pos < val.length() - 1) {
				k = tok.substring(0, pos).trim().toLowerCase();
				v = null;
				if(tok.charAt(pos+1) == '\"') {
					pos2 = tok.lastIndexOf('\"');
					if(pos2 > pos+1)
						v = tok.substring(pos+2, pos2);
				} else {
					v = tok.substring(pos+1);
				}
				if(v != null)
					attrs.put(k, v);
			}
		}
		return attrs;
	}

	static void deleteTemporaryFiles(Hashtable params) {
		Enumeration		keys;
		Object[]		vals;
		int				i;

		keys = params.keys();
		while(keys.hasMoreElements()) {
			vals = (Object[]) params.get(keys.nextElement());
			for(i = 0; i < vals.length; i++) {
				if(vals[i] instanceof UploadedFile) {
					((UploadedFile) vals[i]).deleteTemporaryFile();
				}
			}
		}
	}
}
