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

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * Tests {@code Model}.
 * 
 * @author Benjamin Bentmann
 * @version $Id: ModelTest.java 727548 2008-12-17 22:26:15Z bentmann $
 */
public class ModelTest
{

    @Test
    public void testHashCodeNullSafe()
    {
        new Model().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertFalse( new Model().equals( null ) );
    }

    @Test
    public void testEqualsIdentity()
    {
        Model thing = new Model();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Model().toString() );
    }

}
