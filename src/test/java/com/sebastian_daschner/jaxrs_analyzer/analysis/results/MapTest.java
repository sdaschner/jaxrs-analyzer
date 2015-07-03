package com.sebastian_daschner.jaxrs_analyzer.analysis.results;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author Sebastian Daschner
 */
public class MapTest {

    @Test
    @Ignore
    // TODO un-comment
    public void testMap() throws NotFoundException {
        final CtClass ctClass = ClassPool.getDefault().get("java.util.Map<java.lang.String,java.lang.String>");
        System.out.println(Arrays.toString(ctClass.getMethods()));
    }
}
