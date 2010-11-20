/*
 * Copyright 2008-2010 Digital Enterprise Research Institute (DERI)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sindice.rdfcommons.beanmapper.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * In a a method of a bean is marked with this annotation,
 * then the serialization is performed no more on the bean itself
 * but on the object returned by the annotated method.
 * The method is expected to not accept parameters. 
 *
 * @author Michele Mostarda ( mostarda@fbk.eu )
 * @version $Id$
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( {ElementType.METHOD})
public @interface Adapt {}
