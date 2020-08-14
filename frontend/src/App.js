import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import Websocket from 'react-websocket'

class App extends Component {

  constructor(props) {
    super(props);
    this.state ={messages: []};
    this.addMessage = this.addMessage.bind(this);
  }

  addMessage(jsonData) {
    var messageList = this.state.messages;
    messageList.push(jsonData);
    this.setState({messages: messageList});
  }


  componentDidMount() {
    var topic = 'log-streamer-out';  // TODO: parameterize in environment, or input, or configmap
    var proxyPublicAddress = "127.0.0.1"; // TODO: parameterize in environment, or input, or configmap 
    var proxyPublicPort = "9998";  // TODO: parameterize in environment, or input, or configmap 
    var wsUri = 'ws://'+proxyPublicAddress+':'+proxyPublicPort+'/?topic='+topic+'&consumerGroup=group1'; // URL to kafka-proxy-ws/server.js
    var kafkaSocket = new WebSocket(wsUri);
    var self=this;  // cache "this" as "self" for use in onmessage below 
    kafkaSocket.onmessage = function (event) {
      if( typeof(event.data) === 'string' ) {
        var dataNoBrackets = event.data.substring(1, event.data.length-1); // odd fix: event.data="[json]", so remove brackets from ends.
        console.log("dataNoBrackets="+dataNoBrackets);
        var jsonData = JSON.parse(dataNoBrackets);
        self.addMessage(jsonData);
      } else {
        console.log("error: not string data. typeof(event.data)="+typeof(event.data));
      }
    }
  }

  render() {

    var messageList = this.state.messages.map(function(jsonKafka,i) {
      console.log("adding jsonKafka.message = "+jsonKafka.message);
      return (<tr key={i}>  <td>message={jsonKafka.message}</td>  </tr>);
    });

    return (

      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <p>
            Edit <code>src/App.js</code> and save to reload.
          </p>
          <a
              className="App-link"
              href="https://reactjs.org"
              target="_blank"
              rel="noopener noreferrer"
            >
          </a>

          <table >
            <thead>
              <tr>
                <th colspan="2">Message List Table : </th>
              </tr>
            </thead>
            <tbody>
              {messageList}
            </tbody>
          </table>

        </header>

      </div>
    );
  }

}

export default App;
