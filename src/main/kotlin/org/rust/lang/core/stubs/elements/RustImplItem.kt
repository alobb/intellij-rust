package org.rust.lang.core.stubs.elements


import com.intellij.psi.stubs.*
import org.rust.lang.core.psi.RustImplItemElement
import org.rust.lang.core.psi.impl.RustImplItemElementImpl
import org.rust.lang.core.psi.impl.mixin.asRustPath
import org.rust.lang.core.resolve.indexes.RustImplIndex
import org.rust.lang.core.stubs.RustElementStub
import org.rust.lang.core.stubs.RustStubElementType
import org.rust.lang.core.symbols.RustPath
import org.rust.lang.core.symbols.readRustPath
import org.rust.lang.core.symbols.writeRustPath
import org.rust.lang.core.types.RustUnknownType
import org.rust.lang.core.types.unresolved.RustUnresolvedType
import org.rust.lang.core.types.unresolved.readRustUnresolvedType
import org.rust.lang.core.types.unresolved.writeRustUnresolvedType
import org.rust.lang.core.types.util.type
import org.rust.utils.readNullable
import org.rust.utils.writeNullable

object RustImplItemStubElementType : RustStubElementType<RustImplItemElementStub, RustImplItemElement>("IMPL_ITEM") {

    override fun createStub(psi: RustImplItemElement, parentStub: StubElement<*>?): RustImplItemElementStub =
        RustImplItemElementStub(parentStub, this, psi.type?.type ?: RustUnknownType, psi.traitRef?.path?.asRustPath)

    override fun createPsi(stub: RustImplItemElementStub): RustImplItemElement =
        RustImplItemElementImpl(stub, this)

    override fun deserialize(dataStream: StubInputStream, parentStub: StubElement<*>?): RustImplItemElementStub {
        val type        = dataStream.readRustUnresolvedType()
        val traitRef    = dataStream.readNullable { readRustPath() }

        return RustImplItemElementStub(parentStub, this, type, traitRef)
    }

    override fun serialize(stub: RustImplItemElementStub, dataStream: StubOutputStream) {
        dataStream.writeRustUnresolvedType(stub.type)
        dataStream.writeNullable(stub.traitRef) { writeRustPath(it) }
    }

    override fun indexStub(stub: RustImplItemElementStub, sink: IndexSink) {
        RustImplIndex.ByType.index(stub, sink)
        RustImplIndex.ByName.index(stub, sink)
    }

}

class RustImplItemElementStub(
    parent: StubElement<*>?,
    elementType: IStubElementType<*, *>,
    val type: RustUnresolvedType,
    val traitRef: RustPath?
) : RustElementStub<RustImplItemElement>(parent, elementType)
