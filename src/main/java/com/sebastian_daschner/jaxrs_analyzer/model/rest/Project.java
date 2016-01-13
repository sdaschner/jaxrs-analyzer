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
    private final boolean renderSwaggerTags;
    private final int swaggerTagsPathOffset;

    public Project(final String name, final String version, final String domain, final Resources resources,
            final boolean renderSwaggerTags, final int swaggerTagsPathOffset) {
        StringUtils.requireNonBlank(name);
        StringUtils.requireNonBlank(version);
        StringUtils.requireNonBlank(domain);
        this.name = name;
        this.version = version;
        this.domain = domain;
        this.resources = resources;
        this.renderSwaggerTags = renderSwaggerTags;
        this.swaggerTagsPathOffset = swaggerTagsPathOffset;
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

    public boolean isRenderSwaggerTags()
    {
        return renderSwaggerTags;
    }

    public int getSwaggerTagsPathOffset()
    {
        return swaggerTagsPathOffset;
    }
}
