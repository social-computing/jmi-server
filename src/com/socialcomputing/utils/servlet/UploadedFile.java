package com.socialcomputing.utils.servlet;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * The UploadedFile class represents files uploaded with a multipart/form-data
 * form. The files are stored either in memory or on disk. UploadedFile objects
 * are returned by ExtendedRequest.getFileParameter().
 *
 * @see org.gjt.vinny.servlet.ExtendedRequest#getFileParameter
 * @see org.gjt.vinny.servlet.ExtendedRequest#getFileParameterValues
 */
public class UploadedFile implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1761208851095508899L;
	
	String		contentType, contentFilename;
	int			contentLength, expandedContentLength;
	byte[]		bytes;
	File		file;
	boolean		fileHasBeenDeleted = false;

	UploadedFile(String contentType, int contentLength, int expandedContentLength, String contentFilename, byte[] bytes) {
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.expandedContentLength = expandedContentLength;
		this.contentFilename = contentFilename;
		this.bytes = bytes;
		this.file = null;
	}

	UploadedFile(String contentType, int contentLength, int expandedContentLength, String contentFilename, File file) {
		this.contentType = contentType;
		this.contentLength = contentLength;
		this.expandedContentLength = expandedContentLength;
		this.contentFilename = contentFilename;
		this.bytes = null;
		this.file = file;
	}

	/**
	 * Returns the content-type of the file or <CODE>null</CODE> if unknown.
	 * Some browsers don't send content-types for plain text files.
	 *
	 * @return the content-type
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns the length of the file. This is the actual length, not
	 * the value of a header.
	 *
	 * @return the length
	 */
	public int getContentLength() {
		return contentLength;
	}

	public int getExpandedContentLength() {
		return expandedContentLength;
	}

	/**
	 * Returns the client-side filename of the uploaded file or
	 * <CODE>null</CODE> if unknown. If the filename contains
	 * backslashes or forward slashes, only the part
	 * after the last slash is returned.
	 *
	 * @return the useful part of the client-side filename
	 */
	public String getContentFilename() {
		if(contentFilename != null) {
			int		i;

			i = contentFilename.lastIndexOf('\\');
			if(i != -1) {
				return contentFilename.substring(i+1);
			}

			i = contentFilename.lastIndexOf('/');
			if(i != -1) {
				return contentFilename.substring(i+1);
			}
		}

		return contentFilename;
	}

	/**
	 * Returns the full client-side filename of the uploaded file as
	 * it was sent to the server or <CODE>null</CODE> if unknown.
	 *
	 * @return the full client-side filename
	 */
	public String getContentFullFilename() {
		return contentFilename;
	}

	/**
	 * Creates an <CODE>InputStream</CODE> that can be used to read the
	 * contents of the uploaded file.
	 *
	 * @return an <CODE>InputStream</CODE> for the contents
	 */
	public InputStream getInputStream() throws IOException {
		if(bytes != null) {
			return new ByteArrayInputStream(bytes);
		} else {
			return new FileInputStream(file);
		}
	}

	/**
	 * Creates a <CODE>Reader</CODE> that can be used to read the
	 * contents of the uploaded file.
	 *
	 * @return a <CODE>Reader</CODE> for the contents
	 */
	public Reader getReader() throws IOException {
		if(bytes != null) {
			return new InputStreamReader(new ByteArrayInputStream(bytes));
		} else {
			return new FileReader(file);
		}
	}

	/**
	 * Returns the contents as a byte array. If the uploaded file
	 * has been stored in a temporary file, the file is read to
	 * get the complete contents.
	 *
	 * @return the contents of the uploaded file
	 * @exception java.lang.IllegalStateException when an IOException occurred
	 *			while reading the temporary file.
	 */
	public byte[] getBytes() {
		if(bytes != null) {
			return bytes;
		} else {
			DataInputStream		in;
			byte[]				bytes;

			try {
				in = new DataInputStream(new FileInputStream(file));
				try {
					bytes = new byte[contentLength];
					in.readFully(bytes, 0, contentLength);
					return bytes;
				} finally {
					in.close();
				}
			} catch(IOException ioexc) {
				throw new IllegalStateException("could not read data file: " + ioexc.toString());
			}
		}
	}

	/**
	 * Returns a handle to the temporary file that stores the
	 * uploaded file. This file may disappear when
	 * ExtendedRequest.deleteTemporaryFiles() or UploadedFile.deleteTemporaryFile()
	 * is called, so use with care.
	 *
	 * @exception java.lang.IllegalStateException when the contents
	 *			have been saved in memory.
	 */
	public File getTemporaryFile() {
		if(file != null) {
			return file;
		} else {
			throw new IllegalStateException("no temporary file");
		}
	}

	/**
	 * Deletes any temporary file associated with this uploaded file.
	 * When called a second time or for an uploaded file whose contents
	 * are stored in memory, nothing is done.
	 */
	public void deleteTemporaryFile() {
		if(file != null && !fileHasBeenDeleted) {
			fileHasBeenDeleted = true;
			file.delete();
		}
	}

	/**
	 * Returns the full client-side filename of the uploaded file.
	 *
	 * @return the full client-side filename of the uploaded file
	 */
	public String toString() {
		return contentFilename;
	}
}
