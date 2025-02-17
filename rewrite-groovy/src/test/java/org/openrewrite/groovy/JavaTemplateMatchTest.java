/*
 * Copyright 2023 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.groovy;

import org.junit.jupiter.api.Test;
import org.openrewrite.ExecutionContext;
import org.openrewrite.java.JavaTemplate;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;
import org.openrewrite.marker.SearchResult;
import org.openrewrite.test.RewriteTest;

import static org.openrewrite.groovy.Assertions.groovy;
import static org.openrewrite.test.RewriteTest.toRecipe;

public class JavaTemplateMatchTest implements RewriteTest {


    @Test
    void nonJavaCode() {
        rewriteRun(
          spec -> spec.recipe(toRecipe(() -> new JavaVisitor<>() {
              private final JavaTemplate template = JavaTemplate.builder(
                "\"a\" + \"b\""
              ).build();

              @Override
              public J visitExpression(Expression expression, ExecutionContext ctx) {
                  return expression.getMarkers().findFirst(SearchResult.class).isEmpty() && template.matches(getCursor()) ?
                    SearchResult.found(expression) : super.visitExpression(expression, ctx);
              }
          })),
          groovy(
            //language=groovy
            """
              class T {
                  def foo = "${1}"
              }
              """
          )
        );
    }
}
