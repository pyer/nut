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
 * Tests {@code Build}.
 * 
 * @author Benjamin Bentmann
 * @version $Id: BuildTest.java 727548 2008-12-17 22:26:15Z bentmann $
 */
public class BuildTest
{
    @Test
    public void testOutputDirectory()
    {
        String dir = "directory";
        Build thing = new Build();
        thing.setOutputDirectory( dir );
        assertEquals( thing.getOutputDirectory(), dir );
    }

    @Test
    public void testSourceDirectory()
    {
        String dir = "directory";
        Build thing = new Build();
        thing.setSourceDirectory( dir );
        assertEquals( thing.getSourceDirectory(), dir );
    }

    @Test
    public void testTestOutputDirectory()
    {
        String dir = "directory";
        Build thing = new Build();
        thing.setTestOutputDirectory( dir );
        assertEquals( thing.getTestOutputDirectory(), dir );
    }

    @Test
    public void testTestSourceDirectory()
    {
        String dir = "directory";
        Build thing = new Build();
        thing.setTestSourceDirectory( dir );
        assertEquals( thing.getTestSourceDirectory(), dir );
    }

    @Test
    public void testDefaultModelEncoding()
    {
        Build thing = new Build();
        assertEquals( thing.getModelEncoding(), "UTF-8" );
    }

    @Test
    public void testModelEncoding()
    {
        String me = "ASCII";
        Build thing = new Build();
        thing.setModelEncoding( me );
        assertEquals( thing.getModelEncoding(), me );
    }

    @Test
    public void testHashCodeNullSafe()
    {
        new Build().hashCode();
    }

    @Test
    public void testEqualsNullSafe()
    {
        assertFalse( new Build().equals( null ) );
    }

    @Test
    public void testEqualsIdentity()
    {
        Build thing = new Build();
        assertTrue( thing.equals( thing ) );
    }

    @Test
    public void testToStringNullSafe()
    {
        assertNotNull( new Build().toString() );
    }

}
