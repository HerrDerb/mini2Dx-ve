/**
 * Copyright (c) 2015 See AUTHORS file
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the mini2Dx nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.mini2Dx.ios.di;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mini2Dx.core.di.ComponentScanner;
import org.mini2Dx.core.di.annotation.Prototype;
import org.mini2Dx.core.di.annotation.Singleton;
import org.reflections.Reflections;

/**
 * iOS implementation of {@link ComponentScanner}
 * 
 * @author Thomas Cashman
 */
public class IOSComponentScanner implements ComponentScanner {
    private List<Class<?>> singletonClasses;
    private List<Class<?>> prototypeClasses;

    /**
     * Constructor
     */
    public IOSComponentScanner() {
        singletonClasses = new ArrayList<Class<?>>();
        prototypeClasses = new ArrayList<Class<?>>();
    }

    /**
     * Scans multiple packages recursively for {@link Singleton} and
     * {@link Prototype} annotated classes
     * 
     * @param packageNames
     *            The package name to scan through, e.g. org.mini2Dx.component
     * @throws IOException
     */
    public void scan(String[] packageNames) throws IOException {
        for (String packageName : packageNames) {
            scan(packageName);
        }
    }

    /**
     * Scans a package recursively for {@link Singleton} and {@link Prototype}
     * annotated classes
     * 
     * @param packageName
     *            The package name to scan through, e.g. org.mini2Dx.component
     * @throws IOException
     */
    private void scan(String packageName) throws IOException {
        Reflections reflections = new Reflections(packageName);
        Set<Class<?>> singletons = reflections
                .getTypesAnnotatedWith(Singleton.class);
        singletonClasses.addAll(singletons);

        Set<Class<?>> prototypes = reflections
                .getTypesAnnotatedWith(Prototype.class);
        prototypeClasses.addAll(prototypes);
    }

    public List<Class<?>> getSingletonClasses() {
        return singletonClasses;
    }

    public List<Class<?>> getPrototypeClasses() {
        return prototypeClasses;
    }
}
