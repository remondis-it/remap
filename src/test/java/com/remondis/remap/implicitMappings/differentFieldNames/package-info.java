/**
 * An implicit mapping on different field is currently not supported. Maybe there is a way to relax the type restriction
 * for the reassing operation. Currently reassing only works for equally typed properties with different names. One can
 * think about enabling reassing to look for a registered mapper if the types differ. In this case the API for reassing
 * looses it's type safety to support this.
 */
package com.remondis.remap.implicitMappings.differentFieldNames;
