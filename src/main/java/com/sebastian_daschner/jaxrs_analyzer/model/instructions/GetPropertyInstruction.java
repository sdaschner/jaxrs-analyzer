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

package com.sebastian_daschner.jaxrs_analyzer.model.instructions;

/**
 * Represents a GET_FIELD/STATIC instruction.
 *
 * @author Sebastian Daschner
 */
public abstract class GetPropertyInstruction implements Instruction {

    private final String containingClass;
    private final String propertyName;
    private final String propertyType;

    protected GetPropertyInstruction(final String containingClass, final String propertyName, final String propertyType) {
        this.containingClass = containingClass;
        this.propertyName = propertyName;
        this.propertyType = propertyType;
    }

    public String getContainingClass() {
        return containingClass;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getPropertyType() {
        return propertyType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final GetPropertyInstruction that = (GetPropertyInstruction) o;

        if (containingClass != null ? !containingClass.equals(that.containingClass) : that.containingClass != null)
            return false;
        if (propertyName != null ? !propertyName.equals(that.propertyName) : that.propertyName != null) return false;
        if (propertyType != null ? !propertyType.equals(that.propertyType) : that.propertyType != null) return false;
        if (getStackSizeDifference() != that.getStackSizeDifference()) return false;

        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        int result = containingClass != null ? containingClass.hashCode() : 0;
        result = 31 * result + getStackSizeDifference();
        result = 31 * result + (getType() != null ? getType().ordinal() : 0);
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        result = 31 * result + (propertyType != null ? propertyType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GetPropertyInstruction{" +
                "type='" + getType() + '\'' +
                ", containingClass='" + containingClass + '\'' +
                ", propertyName='" + propertyName + '\'' +
                ", propertyType='" + propertyType + '\'' +
                '}';
    }

}
