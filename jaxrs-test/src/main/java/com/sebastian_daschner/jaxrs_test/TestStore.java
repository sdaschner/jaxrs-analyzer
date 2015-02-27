package com.sebastian_daschner.jaxrs_test;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class TestStore {

    public Model getModel(final String id) {
        return null;
    }

    public String addModel(final Model model) {
        return null;
    }

    public List<Model> getModels() {
        return null;
    }

    public void delete(final String id) {
        // do nothing
    }

}
