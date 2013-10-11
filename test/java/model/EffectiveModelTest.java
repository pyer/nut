package nut.model;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import junit.framework.TestCase;
import nut.model.Model;
import java.io.IOException;

public class EffectiveModelTest
    extends TestCase
{
    private String ref = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<project>\n  <packaging>modules</packaging>\n</project>\n";

    public void testHashCodeNullSafe()
    {
        Model m = new Model();
        new EffectiveModel(m).hashCode();
    }

    public void testEqualsNullSafe()
    {
        Model m = new Model();
        assertFalse( new EffectiveModel(m).equals( null ) );
    }

    public void testEqualsIdentity()
    {
        Model m = new Model();
        EffectiveModel thing = new EffectiveModel(m);
        assertTrue( thing.equals( thing ) );
    }

    public void testToStringNullSafe()
    {
        Model m = new Model();
        assertNotNull( new EffectiveModel(m).toString() );
    }

    public void testGetEffectiveModel()
    {
        Model m = new Model();
        EffectiveModel em = new EffectiveModel(m);
        try {
           assertEquals( em.getEffectiveModel(), ref );
        }
        catch( IOException e ){
           fail("IO Exception");
        }
    }
}
