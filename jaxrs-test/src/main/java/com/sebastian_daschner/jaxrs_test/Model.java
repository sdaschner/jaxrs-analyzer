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

package com.sebastian_daschner.jaxrs_test;

import javax.persistence.*;

/**
 * These sources are solely used for test purposes and not meant for deployment.
 */
@Entity
@Table(name = "models")
@NamedQuery(name = Model.FIND_ALL, query = "select m from Model m")
public class Model {

    public static final String FIND_ALL = "Model.findAll";

    @Id
    @GeneratedValue
    private long id;

    @Basic(optional = false)
    private String name;

    public Model() {
        // nothing to do
    }

    public Model(final String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
