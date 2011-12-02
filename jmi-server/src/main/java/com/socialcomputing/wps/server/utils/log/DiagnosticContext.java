package com.socialcomputing.wps.server.utils.log;

/**
 * 
 * <p>Log diagnostic context helper</p>
 *
 */
public final class DiagnosticContext {
    public final static DiagnosticContext ENTRY_POINT_CTX =
        new DiagnosticContext("entryPoint",
                              "Service entry point accessed");
    public final static DiagnosticContext[] DIAGNOSTIC_CONTEXTS = {
        ENTRY_POINT_CTX,
    };
    
    public final String name;
    public final String desc;

    public DiagnosticContext(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return this.name;
    }
}