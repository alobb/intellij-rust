package org.rust.lang.core.types

import org.rust.lang.core.types.visitors.RustTypeVisitor
import org.rust.lang.core.types.visitors.RustUnresolvedTypeVisitor

object RustBooleanType : RustPrimitiveTypeBase() {

    fun deduce(text: String): RustBooleanType? =
        if (text == "bool") RustBooleanType else null

    override fun <T> accept(visitor: RustTypeVisitor<T>): T = visitor.visitBoolean(this)

    override fun <T> accept(visitor: RustUnresolvedTypeVisitor<T>): T = visitor.visitBoolean(this)

    override fun toString(): String = "bool"

}
