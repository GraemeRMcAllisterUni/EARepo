package gppDemos.nQueensProblem.cluster

import gppLibrary.GroupDetails
import gppLibrary.cluster.NodeInterface
import gppLibrary.functionals.evolutionary.Client
import groovyJCSP.*
import jcsp.lang.*

class NodeClientGroup implements NodeInterface {
    
    ChannelInputList inputList
    ChannelOutputList outputList
    GroupDetails clientDetails = null  // one entry per client MUST be present
    int requiredParents = -1
    int resultantChildren = -1
    int initialPopulation = 0
    String evolveFunction = ""
    String createIndividualFunction = "" 
    int clients = -1

    @Override
    public void run() {
        assert (clientDetails != null): "ClientGroup: clientDetails MUST be specified"
        assert (clients == clientDetails.workers): "ClientGroup: Number of workers mismatch, Process exepcted $clients, Details specified ${clientDetails.workers}"
        List network = (0 ..< clients).collect {c ->
            new Client (
                    receive: (ChannelInput)inputList[c],
                    send: (ChannelOutput)outputList[c],
                    clientDetails: clientDetails.groupDetails[c],
                    evolveFunction: evolveFunction,
                    createIndividualFunction: createIndividualFunction,
                    clientId: c,
                    initialPopulation: initialPopulation,
                    requiredParents: requiredParents,
                    resultantChildren: resultantChildren)
        }
        new PAR (network).run()
    }

    @Override
    public void connect(ChannelInputList inChannels, ChannelOutputList outChannels) {
        inputList = inChannels
        outputList = outChannels        
    }

}
