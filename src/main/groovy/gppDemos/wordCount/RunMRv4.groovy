package gppDemos.wordCount

import gppLibrary.DataDetails
import gppLibrary.mapReduce.Map
import gppLibrary.mapReduce.Partition
import gppLibrary.terminals.Emit
import gppLibrary.terminals.TestPoint
import groovyJCSP.ChannelInputList
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.Channel
import gppDemos.wordCount.WordBuffer as wb
import gppDemos.wordCount.MapData as md
import gppDemos.wordCount.PartitionData as pd
import gppDemos.wordCount.ReduceData as rd

int blockSize = 500
int partitions = 3
def title = "ACMmr"
def fileName = "src\\demos\\wordCount\\${title}.txt"
def fileName2 = "src\\demos\\wordCount\\${title}2.txt"

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()
def chan3 = Channel.one2oneArray(partitions* 2)
def chan3AOutList = new ChannelOutputList()
def chan3BOutList = new ChannelOutputList()
def chan3AInList = new ChannelInputList()
def chan3BInList = new ChannelInputList()
def chan3CInList = new ChannelInputList()
def chan4 = Channel.one2one()
def chan5 = Channel.one2one()
def chan6 = Channel.one2oneArray(partitions)
def chan6InList = new ChannelInputList(chan6)

for ( p in 0 .. 2){
    chan3AOutList.append( chan3[p].out())
    chan3BOutList.append( chan3[ p+ partitions].out())
}

chan3AInList.append(chan3[0].in())
chan3AInList.append(chan3[3].in())
chan3BInList.append(chan3[1].in())
chan3BInList.append(chan3[4].in())
chan3CInList.append(chan3[2].in())
chan3CInList.append(chan3[5].in())


def eDetails = new DataDetails( dName: wb.getName(),
                                dInitMethod: wb.init,
                                dCreateMethod: wb.create,
                                dInitData: [fileName, blockSize])

def eDetails2 = new DataDetails( dName: wb.getName(),
                                dInitMethod: wb.init,
                                dCreateMethod: wb.create,
                                dInitData: [fileName2, blockSize])

def pDetails = new DataDetails( dName: pd.getName(),
                                dInitMethod: pd.initClass,
                                dInitData: [partitions, " " .. "f", "g" .. "n", "o" .. "~" ])

def reduceDetails = new DataDetails(dName: rd.getName(),
                                    dInitMethod: rd.initClass)

def network = []

network << new Emit( eDetails: eDetails,
                     output: chan1.out())

network << new Emit( eDetails: eDetails2,
                     output: chan4.out())

network << new Map( input: chan1.in(),
                    output: chan2.out(),
                    outClassName: md.getName(),
                    initClass: md.initClass,
                    mapFunction: md.mapFunction)

network << new Map( input: chan4.in(),
                    output: chan5.out(),
                    outClassName: md.getName(),
                    initClass: md.initClass,
                    mapFunction: md.mapFunction)

network << new Partition( input: chan2.in(),
                          outList: chan3AOutList,
                          pDetails: pDetails,
                          partitionFunction: pd.partitionFunction,
                          getPartition: pd.getPartition,
                          partitions: partitions)

network << new Partition( input: chan5.in(),
                          outList: chan3BOutList,
                          pDetails: pDetails,
                          partitionFunction: pd.partitionFunction,
                          getPartition: pd.getPartition,
                          partitions: partitions)
/*
network << new Reduce(inList: chan3AInList,
                      output: chan6[0].out(),
                      rDetails: reduceDetails,
                      reduceMethod : rd.reduceMethod,
                      getReduction: rd.getReduction)

network << new Reduce(inList: chan3BInList,
                      output: chan6[1].out(),
                      rDetails: reduceDetails,
                      reduceMethod : rd.reduceMethod,
                      getReduction: rd.getReduction)

network << new Reduce(inList: chan3CInList,
                      output: chan6[2].out(),
                      rDetails: reduceDetails,
                      reduceMethod : rd.reduceMethod,
                      getReduction: rd.getReduction)

*/

for ( p in 0..< partitions*2) {
        network << new TestPoint( input: chan3[p].in() )
}


//network << new TestPoint(input: chan2.in())
//network << new TestPoint(input: chan5.in())


new PAR(network).run()
