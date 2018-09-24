package gppDemos.wordCount

import gppLibrary.DataDetails
import gppLibrary.mapReduce.Map
import gppLibrary.mapReduce.Partition
import gppLibrary.terminals.Emit
import gppLibrary.terminals.TestPoint
import groovyJCSP.ChannelOutputList
import groovyJCSP.PAR
import jcsp.lang.Channel
import gppDemos.wordCount.WordBuffer as wb
import gppDemos.wordCount.MapData as md
import gppDemos.wordCount.PartitionData as pd

int blockSize = 500
def partitions = 3
def title = "ACMmr"
def fileName = "./${title}.txt"

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()
def chan3 = Channel.one2oneArray(partitions)
def chan3OutList = new ChannelOutputList(chan3)

def eDetails = new DataDetails( dName: wb.getName(),
                                dInitMethod: wb.init,
                                dCreateMethod: wb.create,
                                dInitData: [fileName, blockSize])

def pDetails = new DataDetails( dName: pd.getName(),
                                dInitMethod: pd.initClass,
                                dInitData: [partitions, " " .. "f", "g" .. "n", "o" .. "~" ])

def network = []

network << new Emit( eDetails: eDetails,
                     output: chan1.out())

network << new Map( input: chan1.in(),
                    output: chan2.out(),
                    outClassName: md.getName(),
                    initClass: md.initClass,
                    mapFunction: md.mapFunction)

network << new Partition( input: chan2.in(),
                          outList: chan3OutList,
                          pDetails: pDetails,
                          partitionFunction: pd.partitionFunction,
                          getPartition: pd.getPartition,
                          partitions: partitions)

for ( p in 0..< partitions) {
    network << new TestPoint( input: chan3[p].in() )
}

new PAR(network).run()
