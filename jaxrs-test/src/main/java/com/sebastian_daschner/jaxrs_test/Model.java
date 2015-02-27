package com.sebastian_daschner.jaxrs_test;

import javax.persistence.*;

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
