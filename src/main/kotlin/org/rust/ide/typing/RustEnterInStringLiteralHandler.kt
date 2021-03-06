package org.rust.ide.typing

import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate.Result
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiFile
import com.intellij.psi.StringEscapesTokenTypes.STRING_LITERAL_ESCAPES
import org.rust.lang.core.psi.RustTokenElementTypes.RAW_LITERALS
import org.rust.lang.core.psi.RustTokenElementTypes.STRING_LITERALS
import org.rust.lang.core.psi.impl.RustFile

class RustEnterInStringLiteralHandler : EnterHandlerDelegateAdapter() {
    override fun preprocessEnter(
        file: PsiFile,
        editor: Editor,
        caretOffsetRef: Ref<Int>,
        caretAdvanceRef: Ref<Int>,
        dataContext: DataContext,
        originalHandler: EditorActionHandler?
    ): Result {
        // Return if this is not a Rust file
        if (file !is RustFile) {
            return Result.Continue
        }

        val caretOffset = caretOffsetRef.get()
        if (!isValidInnerOffset(caretOffset, editor.document.charsSequence)) return Result.Continue

        val highlighter = (editor as EditorEx).highlighter
        val iterator = highlighter.createIterator(caretOffset)

        // Return if we are not inside literal contents (i.e. in prefix, suffix or delimiters)
        if (!RustQuoteHandler().isDeepInsideLiteral(iterator, caretOffset)) return Result.Continue

        // Return if we are inside escape sequence
        if (iterator.tokenType in STRING_LITERAL_ESCAPES) {
            // If we are just at the beginning, we don't want to return, but
            // we have to determine literal type. Retreating iterator will do.
            if (caretOffset == iterator.start) {
                iterator.retreat()
            } else {
                return Result.Continue
            }
        }

        return when (iterator.tokenType) {
            in STRING_LITERALS -> {
                // If we are inside string literal, add trailing '\' just before caret
                editor.document.insertString(caretOffset, "\\")
                caretOffsetRef.set(caretOffset + 1)
                Result.DefaultForceIndent
            }

            in RAW_LITERALS ->
                Result.DefaultSkipIndent

            else -> Result.Continue
        }
    }
}
