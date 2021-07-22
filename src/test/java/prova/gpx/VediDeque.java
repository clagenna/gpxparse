package prova.gpx;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.Test;

public class VediDeque {

  @Test
  public void provalo() {
    Deque<String> que = new ArrayDeque<String>();
    que.push("obama");
    que.push("trump");
    que.push("biden");
    que.push("claudio");
    String sz = que.stream().collect(Collectors.joining("/"));
    System.out.println("VediDeque.provalo()=" + sz);
    // wrong: claudio/biden/trump/obama
    Iterator<String> iter = que.descendingIterator();
    sz = StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, 0), false) //
        .collect(Collectors.joining("/"));
    System.out.println("VediDeque.provalo()=" + sz);
    // good!: obama/trump/biden/claudio
  }

}
