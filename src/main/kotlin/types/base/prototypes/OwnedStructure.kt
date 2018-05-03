package types.base.prototypes

import types.base.prototypes.structures.Owner
import types.base.prototypes.structures.Structure

open external class OwnedStructure : Structure {
    val my: Boolean
    val owner: Owner
}