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

                // respond is - a universal response, which consists, of a payload, containing,

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