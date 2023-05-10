package com.remondis.remap.utils.property;

public enum ChangeType {
  NONE,
  ADD_NEW,
  REMOVE_ORPHANS,
  ALL;

  public boolean isAddNew() {
    return ADD_NEW.equals(this) || ALL.equals(this);
  }

  public boolean isRemoveOrphans() {
    return REMOVE_ORPHANS.equals(this) || ALL.equals(this);
  }
}
