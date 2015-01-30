# Peapod for Tinkerpop 3 <a href="https://bayofmany.ci.cloudbees.com/"><img src="https://www.cloudbees.com/sites/default/files/styles/large/public/Button-Built-on-CB-1.png" width="150" align="right"></a>

> The project's name refers to the java class encapsulation of graph vertices. <br />
> It's a just like peapods wrapping peas.

An object-graph wrapper (OGW) for the Tinkerpop 3 graph stack. This project has been created as an alternative to the Frames module in Tinkerpop 2. Complete documentation can be found [here](http://bayofmany.github.io).


This project is similar to the [Totorom](https://github.com/BrynCooke/totorom) library created by Bryn Cook and the [Ferma](https://github.com/Syncleus/Ferma) library created by Jeffrey Phillips Freeman. The main differences are:
* Peapod is based upon Tinkerpop 3.
* Like Tinkerpop 2 Frames, Peapod uses code generation to implement the framed vertex and edge classes. Unlike Frames and Ferma, the code is generated at compile-time using annotation processors. The generated java source code is completely transparent. There is no code generation at runtime, so no additional startup cost, and the use of reflection is limited in order to have minimal performance overhead.
* In a next iteration, the peapod generator will generate a full DSL to traverse your domain model using the [Traversal API in Tinkerpop 3](http://www.tinkerpop.com/docs/3.0.0.M7/#_domain_specific_languages)

To integrate peapod in your project, include the following dependency. <br /> 
(Disclaimer: The 0.1.0 version is an experimental initial release).

    <dependency>
        <groupId>org.bayofmany.peapod</groupId>
        <artifactId>peapod</artifactId>
        <version>0.1.0</version>
    </dependency>

<img src="http://www.tinkerpop.com/docs/3.0.0.M7/images/tinkerpop-classic.png" width="400" >

This way you define the framed vertices and edges:

    @Vertex
    public abstract class Person {
      public abstract String getName();
      public abstract void setName(String name);

      public abstract List<Knows> getKnows();
      public abstract Knows getKnows(Person person);
      public abstract Knows addKnows(Person person);
      public abstract Knows removeKnows(Person person);
    }
    
    @Edge
    public abstract class Knows {
      public abstract void setYears(int years);
      public abstract int getYears();
    }

    
And this way you query for and interact with the framed objects:
    
    public void testClassic() {
        Graph g = TinkerFactory.createClassic();
        FramedGraph graph = new FramedGraph(g);

        Person marko = graph.v(1, Person.class);
        assertEquals("marko", marko.getName());

        Person vadas = graph.v(2, Person.class);
        Person josh = graph.v(4, Person.class);

        List<Person> result = graph.V(Person.class).has("name", "josh").toList();
        assertThat(result, contains(josh));

        assertThat(marko.getKnows(), containsInAnyOrder(vadas, josh));
    }
    
This project uses code derived from the [Tinkerpop](http://www.tinkerpop.com/) project under the Apache license and/or Tinkerpop license.
