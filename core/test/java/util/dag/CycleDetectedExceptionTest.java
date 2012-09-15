package nut.util.dag;

/*
 * Copyright The Codehaus Foundation.
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

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: CycleDetectedExceptionTest.java 8010 2009-01-07 12:59:50Z vsiveton $
 */
public class CycleDetectedExceptionTest
        extends TestCase
{
    public void testException()
    {
        final List cycle = new ArrayList();

        cycle.add( "a" );

        cycle.add( "b" );

        cycle.add( "a" );

        final CycleDetectedException e = new CycleDetectedException( "Cycle detected", cycle );

        assertEquals( "Cycle detected a --> b --> a", e.getMessage() );
    }
}
