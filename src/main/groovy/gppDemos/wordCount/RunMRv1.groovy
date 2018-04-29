package gppDemos.wordCount

import gppLibrary.DataDetails
import gppLibrary.mapReduce.Map
import gppLibrary.terminals.Emit
import gppLibrary.terminals.TestPoint
import groovyJCSP.PAR
import jcsp.lang.Channel
import gppDemos.wordCount.WordBuffer as wb
import gppDemos.wordCount.MapData as md

int blockSize = 500
def title = "ACMmr"
def fileName = "src\\demos\\wordCount\\${title}.txt"

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()

def eDetails = new DataDetails( dName: wb.getName(),
                                dInitMethod: wb.init,
                                dCreateMethod: wb.create,
                                dInitData: [fileName, blockSize])

def network = []

network << new Emit( eDetails: eDetails,
                     output: chan1.out())

network << new Map( input: chan1.in(),
                    output: chan2.out(),
                    outClassName: md.getName(),
                    initClass: md.initClass,
                    mapFunction: md.mapFunction)

network  << new TestPoint( input: chan2.in() )

new PAR(network).run()
