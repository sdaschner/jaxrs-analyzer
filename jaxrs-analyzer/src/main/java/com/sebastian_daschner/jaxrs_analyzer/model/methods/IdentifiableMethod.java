/*
 * Copyright (C) 2015 Sebastian Daschner, sebastian-daschner.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sebastian_daschner.jaxrs_analyzer.model.methods;

/**
 * Represents a method which is identifiable for a class name, method name and parameter types.
 *
 * @author Sebastian Daschner
 */
public interface IdentifiableMethod extends Method {

    /**
     * Checks if the given signature matches this method.
     *
     * @param identifier The method signature
     * @return {@code true} if this method matches the signature
     */
    boolean matches(final MethodIdentifier identifier);

}
