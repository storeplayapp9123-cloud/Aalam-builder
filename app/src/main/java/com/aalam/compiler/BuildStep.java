package com.aalam.compiler;

/** Ek build step. Fail hone par Exception throw karo, pipeline ruk jayegi. */
public interface BuildStep {
    String name();

    void run(BuildContext ctx) throws Exception;
}
