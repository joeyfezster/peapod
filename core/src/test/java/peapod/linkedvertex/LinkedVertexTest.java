/*
 * Copyright 2015 Bay of Many
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 * This project is derived from code in the Tinkerpop project under the following license:
 *
 *    Tinkerpop3
 *    http://www.apache.org/licenses/LICENSE-2.0
 */

package peapod.linkedvertex;

import com.tinkerpop.gremlin.process.T;
import com.tinkerpop.gremlin.structure.Graph;
import com.tinkerpop.gremlin.structure.Vertex;
import com.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.junit.Before;
import org.junit.Test;
import peapod.FramedGraph;

import java.util.List;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class LinkedVertexTest {

    private Person alice;
    private Person bob;
    private Person charlie;

    private FramedGraph graph;

    @Before
    public void init() {
        Graph g = TinkerGraph.open();
        Vertex alice = g.addVertex(T.id, 1, T.label, "Person", "name", "alice");
        Vertex bob = g.addVertex(T.id, 2, T.label, "Person", "name", "bob");
        Vertex charlie = g.addVertex(T.id, 3, T.label, "Person", "name", "charlie");

        alice.addEdge("friend", bob);
        alice.addEdge("friend", charlie);

        graph = new FramedGraph(g);
        this.alice = graph.v(1, Person.class);
        this.bob = graph.v(2, Person.class);
        this.charlie = graph.v(3, Person.class);
    }

    @Test
    public void testOutVertices() {
        assertEquals(2, alice.getFriends().size());
        Person bob = graph.v(2, Person.class);
        assertThat(alice.getFriends(), hasItems(bob, graph.v(3, Person.class)));
        assertEquals(0, bob.getFriends().size());
    }

    @Test
    public void testLinkedVerticesWithDefaultAnnotation() {
        assertEquals(2, alice.getFriendsWithAnnotationDefault().size());
        assertThat(alice.getFriends(), hasItems(graph.v(2, Person.class), graph.v(3, Person.class)));
    }

    @Test
    public void testLinkedVerticesWithDefaultAnnotationOut() {
        assertEquals(2, alice.getFriendsWithAnnotationOut().size());
        assertThat(alice.getFriends(), hasItems(graph.v(2, Person.class), graph.v(3, Person.class)));
    }

    @Test
    public void testLinkedVerticesWithDefaultAnnotationIn() {
        assertEquals(0, alice.getFriendsWithAnnotationIn().size());

        Person bob = graph.v(2, Person.class);
        assertEquals(1, bob.getFriendsWithAnnotationIn().size());
        assertThat(bob.getFriendsWithAnnotationIn(), hasItems(graph.v(1, Person.class)));
    }

    @Test
    public void testLinkedVerticesWithDefaultAnnotationBoth() {
        Person bob = graph.v(2, Person.class);
        Person steve = graph.v(3, Person.class);

        assertThat(alice.getFriendsWithAnnotationBoth(), hasItems(bob, steve));
        assertThat(bob.getFriendsWithAnnotationBoth(), hasItems(alice));
        assertThat(steve.getFriendsWithAnnotationBoth(), hasItems(alice));
    }

    @Test
    public void testVertexOut() {
        assertThat(alice.out("friend", Person.class).toList(), containsInAnyOrder(bob, charlie));

        List<String> names = alice.out("friend", Person.class).<String>values("name").toList();
        assertThat(names, containsInAnyOrder("bob", "charlie"));

        Set<Person> me = alice.out("friend", Person.class).in("friend", Person.class).toSet();
        assertThat(me, containsInAnyOrder(alice));
    }

    @Test
    public void testVertexIn() {
        List<Person> friends = bob.in("friend", Person.class).toList();
        assertThat(friends, containsInAnyOrder(alice));

        List<String> names = bob.in("friend", Person.class).<String>values("name").toList();
        assertThat(names, containsInAnyOrder("alice"));

        Set<Person> me = bob.in("friend", Person.class).out("friend", Person.class).toSet();
        assertThat(me, containsInAnyOrder(bob, charlie));
    }

}
