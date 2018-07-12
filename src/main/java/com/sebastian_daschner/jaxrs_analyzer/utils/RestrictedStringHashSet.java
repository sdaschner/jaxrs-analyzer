package com.sebastian_daschner.jaxrs_analyzer.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class RestrictedStringHashSet extends HashSet<String> {

    private Set<String> ignored = new HashSet<>();

    public RestrictedStringHashSet() {
        super();
    }

    public RestrictedStringHashSet( Set<String> ignored ) {
        super();
        this.ignored = ignored;
    }

    @Override
    public boolean add( String s ) {
        if( ignored.contains( s ) )
            return false;
        else
            return super.add( s );
    }


    @Override
    public boolean addAll( Collection<? extends String> c ) {
        for( String elem : c ) {
            if( ignored.contains( elem ) )
                return false;
        }
        return super.addAll( c );
    }


    public Set<String> getIgnored() {
        return ignored;
    }
}
