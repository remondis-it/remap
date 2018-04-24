package com.remondis.remap.builder;

public class BuilderModel {

  private Long field;
  private String name;

  private BuilderModel(final BuilderModelBuilder builderModelBuilder) {
    field = builderModelBuilder.field;
    name = builderModelBuilder.name;
  }

  public Long getField() {
    return field;
  }

  public String getName() {
    return name;
  }

  public static class BuilderModelBuilder {

    private Long field;
    private String name;

    public BuilderModelBuilder(final String name) {
      this.name = name;
    }

    public BuilderModelBuilder field(final Long field) {
      this.field = field;
      return this;
    }

    public BuilderModel build() {
      return new BuilderModel(this);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((field == null) ? 0 : field.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BuilderModel other = (BuilderModel) obj;
    if (field == null) {
      if (other.field != null)
        return false;
    } else if (!field.equals(other.field))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }
}
