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

package com.sebastian_daschner.jaxrs_analyzer.analysis.classes.testclasses.resource.response;

import com.sebastian_daschner.jaxrs_analyzer.model.Types;
import com.sebastian_daschner.jaxrs_analyzer.model.elements.HttpResponse;

import javax.ws.rs.core.Response;
import java.util.*;

public class TestClass60 {

    ConfigurationManager configurationManager;

    @javax.ws.rs.GET
    public Response method(final String name) {
        ConfigurationManager.Configuration configuration = this.configurationManager.getConfiguration(name);
        if (configuration == null)
            return Response.noContent().build();
        return Response.ok(configuration).build();
    }

    public static Set<HttpResponse> getResult() {
        final HttpResponse responseFound = new HttpResponse();
        responseFound.getStatuses().add(200);
        responseFound.getEntityTypes().add("Lcom/sebastian_daschner/jaxrs_analyzer/analysis/classes/testclasses/resource/response/TestClass60$ConfigurationManager$Configuration;");
        responseFound.getEntityTypes().add(Types.OBJECT);

        final HttpResponse responseNotFound = new HttpResponse();
        responseNotFound.getStatuses().add(204);

        return new HashSet<>(Arrays.asList(responseFound, responseNotFound));
    }

    private class ConfigurationManager {
        public Configuration getConfiguration(String cacheName) {
            Map<String, String> cache = new HashMap<>();
            if (cache == null) {
                return null;
            }
            Configuration configuration = (Configuration) new Object();
            if (configuration == null) {
                return null;
            }
            boolean active = configuration.isActive();
            return new Configuration(active);
        }

        class Configuration {
            public Configuration() {
            }

            public Configuration(final boolean active) {
            }

            boolean isActive() {
                return new Random().nextBoolean();
            }
        }
    }
}
