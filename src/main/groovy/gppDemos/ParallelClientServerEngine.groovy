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



@CompileStatic
class ParallelClientServerEngine extends DataClass implements CSProcess {

    GroupDetails clientDetails = null  // one entry per client Must not be null
    LocalDetails serverDetails
    int clients = -1
    int initialPopulation = -1
    int requiredParents = -1
    int resultantChildren = -1
    String evolveFunction = ""
    String createIndividualFunction = ""
    String selectParentsFunction = ""
    String incorporateChildrenMethod = ""
    String addIndividualsMethod = ""
    String carryOnFunction = "" // returns true or false but in error null

    String logPhaseName = ""
    String logPropertyName = ""


    void run() {
        assert (clients > 0):
                "ParallelClientServerEngine property clients MUST be greater than 0 actually $clients"
        assert (clientDetails != null):
                "EAClientServerEngine property gDetails MUST NOT be null"
        def serverToClients = Channel.one2oneArray(clients)
        def clientsToServer = Channel.one2oneArray(clients)
        def serverToClientsListOut = new ChannelOutputList(serverToClients)
        def clientsToServerListIn = new ChannelInputList(clientsToServer)
        def clientsToServerListOut = new ChannelOutputList(clientsToServer)
        def serverToClientsListIn = new ChannelInputList(serverToClients)

        def server = new Server(
                request: clientsToServerListIn,
                response: serverToClientsListOut,
                clients: clients,
                serverDetails: serverDetails,
                selectParentsFunction: selectParentsFunction,
                incorporateChildrenMethod: incorporateChildrenMethod,
                addIndividualsMethod: addIndividualsMethod,
                carryOnFunction: carryOnFunction,
                logPhaseName: logPhaseName,
                logPropertyName: logPropertyName)

        def clientNetwork = new ClientGroup(
                outputList: clientsToServerListOut,
                inputList: serverToClientsListIn,
                clientDetails: clientDetails,
                requiredParents: requiredParents,
                resultantChildren: resultantChildren,
                clients: clients,
                initialPopulation: initialPopulation,
                evolveFunction: evolveFunction,
                createIndividualFunction: createIndividualFunction,
                logPhaseName: logPhaseName,
                logPropertyName: logPropertyName)

        new PAR([clientNetwork, server]).run()
    }

}

@CompileStatic
class ClientGroup implements CSProcess {

    ChannelInputList inputList
    ChannelOutputList outputList

    GroupDetails clientDetails = null  // one entry per client MUST be present

    int requiredParents = -1
    int resultantChildren = -1
    int initialPopulation = 0
    String evolveFunction = ""
    String createIndividualFunction = ""
    int clients = -1

    String logPhaseName = ""
    String logPropertyName = ""

    void run() {

        assert (clientDetails != null): "ClientGroup: clientDetails MUST be specified"
        assert (clients == clientDetails.workers): "ClientGroup: Number of workers mismatch, Process exepcted $clients, Details specified ${clientDetails.workers}"
        List network = (0..<clients).collect { c ->
            new Client(
                    receive: (ChannelInput)inputList[c],
                    send: (ChannelOutput)outputList[c],
                    clientDetails: clientDetails.groupDetails [c],
                    evolveFunction: evolveFunction,
                    createIndividualFunction: createIndividualFunction,
                    clientId: c,
                    initialPopulation: initialPopulation,
                    requiredParents: requiredParents,
                    resultantChildren: resultantChildren,
                    logPhaseName: logPhaseName == "" ? "" : (String)"$c, "  + logPhaseName ,
                    logPropertyName: logPropertyName)
        }

        new PAR(network).run()

    }

}

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

        Class clientClass = Class.forName(clientDetails.lName) // creates a class based on the name from GroupDetails in the groovy script will be name of CSP cient or server groovy class
        Object individualInit = clientClass.newInstance()
        callUserMethod(individualInit, clientDetails.lInitMethod, clientDetails.lInitData, 27)


        def initialise = new UniversalRequest(tag: writeRequest, count: initialPopulation)
        for (p in 1..initialPopulation) {
            Object individual = clientClass.newInstance()
            returnCode = individual.&"$createIndividualFunction"()
            if (returnCode == completedOK)
                initialise.individuals << individual   // add an individual created initially by this client
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
                    parameters << clientClass.newInstance()
                }
                boolean result = individualInit.&"$evolveFunction"(parameters) // it is here that we find the limitation that the client only uses 
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


class Server extends DataClass implements CSProcess {

    ChannelInputList request
    ChannelOutputList response
    int clients = -1
    LocalDetails serverDetails
    String selectParentsFunction = ""
    String incorporateChildrenMethod = ""
    String addIndividualsMethod = ""
    String carryOnFunction = "" // returns true or false but in error null

    String logPhaseName = ""
    String logPropertyName = ""

    @CompileStatic
    void runMethod() {
        boolean running = true
        Object inputObject = new Object()
        int returnCode
        int finished = 0

        Class serverClass = Class.forName(serverDetails.lName)
        def server = serverClass.newInstance()         //initialise the server class

        callUserMethod(server, serverDetails.lInitMethod, serverDetails.lInitData, 29) // now read all the initialised individuals into server

        for (c in 0..<clients) {

            def initialPopulation = (UniversalRequest) ((ChannelInput) request[c]).read() // now add the enclosed individuals to the population

            assert (initialPopulation.tag == writeRequest):
                    "Server expecting writeRequest UniversalRequest"

            callUserMethod(server, addIndividualsMethod, initialPopulation.individuals, 30)
        }

        def startSignal = []
        for (i in 0..<clients) startSignal << new UniversalSignal()
        response.write(startSignal) // now send signal in parallel to the clients to start main processing loop




        def alt = new ALT(request) // now create the ALT required to access the requestList

        int index = -1

        while (running) { // running loop

            index = (clients == 1) ? 0 : alt.fairSelect() // if (clients == 1) then index = 0  else index = alt.fairselect()

            def input = (UniversalRequest) ((ChannelInput) request[index]).read() //input is either a UniversalRequest or UniversalResponse

            if (input.tag == readRequest) {

                UniversalResponse respond = server.&"$selectParentsFunction"(input.count)

                assert (respond != null):
                        "Client-Server: Server Process $selectParentsFunction returned null"

                ((ChannelOutput) response[index]).write(respond) // and write the response

            }
            else // must be a List of child individuals
            {
                assert (input.tag == writeRequest):
                        "Client-Server: Server Process expecting request to write evolved children into population"
                //input.individuals.each{println "$it"}
                callUserMethod(server, incorporateChildrenMethod, input.individuals, 31)
            }


            running = server.&"$carryOnFunction"()  // returns false when loop should terminate
            assert (running != null):
                    "Client-Server: Server Process $carryOnFunction returned null"
        } // see if we are terminating



        // now terminate all the clients some of which will still be working on an evolution
        int terminated = 0
        running = true  //while we terminate the process
        while (running) {
            index = (clients == 1) ? 0 : alt.fairSelect()
            def input = (UniversalRequest) ((ChannelInput) request[index]).read()
            //input is either a UniversalRequest or UniversalResponse
            if (input.tag == readRequest) {
                terminated = terminated + 1 // wait until all clients are awaiting a response
            } else { // must be an evolved child being returned
                callUserMethod(server, incorporateChildrenMethod, input.individuals, 31)
            }
            if (terminated == clients) running = false
        }
        // now do server finalisation
        callUserMethod(server, serverDetails.lFinaliseMethod, serverDetails.lFinaliseData, 32)
        // now send signal in parallel to the clients to terminate main processing loop
        def endSignal = []
        for (i in 0..<clients) endSignal << new UniversalTerminator()
        response.write(endSignal)
    }

    void run() {
        if (logPhaseName == "")
            runMethod()
        else {  // getProperty() of this code cannot be compiled statically
            println "logging version of Server Process not yet implemented"
        }
    }
}
