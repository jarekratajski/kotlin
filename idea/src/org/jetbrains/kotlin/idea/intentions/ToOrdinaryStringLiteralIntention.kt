/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.idea.intentions

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.util.text.StringUtil
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

class ToOrdinaryStringLiteralIntention : SelfTargetingOffsetIndependentIntention<KtStringTemplateExpression>(
        KtStringTemplateExpression::class.java,
        "To ordinary string literal"
), LowPriorityAction {
    override fun isApplicableTo(element: KtStringTemplateExpression): Boolean {
        return element.text.startsWith("\"\"\"")
    }

    override fun applyTo(element: KtStringTemplateExpression, editor: Editor?) {
        val text = buildString {
            append("\"")

            for (entry in element.entries) {
                if (entry is KtLiteralStringTemplateEntry) {
                    var text = entry.text
                    text = text.replace("\\", "\\\\")
                    text = text.replace("\"", "\\\"")
                    text = StringUtil.convertLineSeparators(text, "\\n")
                    append(text)
                }
                else {
                    append(entry.text)
                }
            }

            append("\"")
        }
        element.replace(KtPsiFactory(element).createExpression(text))
    }
}