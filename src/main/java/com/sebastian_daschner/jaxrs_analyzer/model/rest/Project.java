package com.sebastian_daschner.jaxrs_analyzer.model.rest;

import com.sebastian_daschner.jaxrs_analyzer.analysis.utils.StringUtils;

/**
 * A REST project.
 *
 * @author Sebastian Daschner
 */
public class Project {

    private final String name;
    private final String version;
    private final String domain;
    private final Resources resources;

    public Project(final String name, final String version, final String domain, final Resources resources) {
        StringUtils.requireNonBlank(name);
        StringUtils.requireNonBlank(version);
        StringUtils.requireNonBlank(domain);
        this.name = name;
        this.version = version;
        this.domain = domain;
        this.resources = resources;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getDomain() {
        return domain;
    }

    public Resources getResources() {
        return resources;
    }

}
