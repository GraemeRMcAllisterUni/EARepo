package gppDemos

import gppLibrary.DataClass
import gppLibrary.GroupDetails
import gppLibrary.LocalDetails
import groovy.transform.CompileStatic
import groovyJCSP.ALT
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.CSProcess
import jcsp.lang.Channel
import jcsp.lang.ChannelInput
import jcsp.lang.ChannelOutput

class Client extends DataClass implements CSProcess {

    ChannelOutput send
    ChannelInput receive
    LocalDetails clientDetails
    int requiredParents = -1
    int resultantChildren = -1
    int initialPopulation = -1
    int clientId = -1
    String evolveFunction = ""  // returns true if evolute runs correctly; all children are expected to be returned
    String createIndividualFunction = ""
    // returns completedOK and used in the creation of initialPopulation individuals
    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        boolean running = true
        Object inputObject = new Object()
        int returnCode
        Class workerClass = Class.forName(clientDetails.lName) // creates a class based on the name from GroupDetails in the groovy script will be name of CSP cient or server groovy class


        def initialise = new UniversalRequest(tag: writeRequest, count: initialPopulation)

        for (p in 0..initialPopulation) {
            clientDetails.lInitData << p
            Object worker = workerClass.newInstance()
            callUserMethod(worker, clientDetails.lInitMethod, clientDetails.lInitData, 27)
            returnCode = worker.&"$createIndividualFunction"()
            if (returnCode == completedOK)
                initialise.individuals << worker   // add an individual created initially by this client
        }

        send.write(initialise) // send created individuals to server

        inputObject = receive.read() // read signal from server to indicate all clients have sent their individuals

        assert (inputObject instanceof UniversalSignal):
                "Client did not receive anticipated response after creating individual(s)"

        while (running) {
            send.write(new UniversalRequest(tag: readRequest, count: requiredParents)) // inform server this client needs parents by sending a UniversalRequest read object

            inputObject = receive.read()  // read response from server

            if (inputObject instanceof UniversalTerminator) running = false


            else {  //response will be a list of requiredParents and children
                assert (inputObject instanceof UniversalResponse):
                        "Client did not receive instance of UniversalResponse"
                List parameters = []
                for (i in 0..<requiredParents) {
                    parameters << ((List) ((UniversalResponse) inputObject).payload)[i]
                }
                for (i in 0..<resultantChildren) {
                    parameters << workerClass.newInstance()
                }

                Object workerInit = workerClass.newInstance()
                callUserMethod(workerInit, clientDetails.lInitMethod, clientDetails.lInitData, 27)
                boolean result = workerInit.&"$evolveFunction"(parameters) // it is here that we find the limitation that the client only uses
                assert (result != null):
                        "Client Process: unexpected error from $evolveFunction"
                if (result) {


                    List children = []
                    for (i in 0..<resultantChildren) children << parameters[requiredParents + i]
                    def sendChildren = new UniversalRequest(tag: writeRequest,
                            count: resultantChildren,
                            individuals: children)
                    send.write(sendChildren)
                }
            }
        } // while loop
    } // run method


    void run() {
        if (logPhaseName == "")
            runMethod()
        else {  // getProperty() of this code cannot be compiled statically
            println "logging version of Client Process not yet implemented"
        }

    }
}



