package gppDemos.wordCount

import gppLibrary.DataDetails
import gppLibrary.terminals.Emit
import gppLibrary.terminals.TestPoint
import groovyJCSP.PAR
import jcsp.lang.Channel
import gppDemos.wordCount.WordBuffer as wb

int blockSize = 500
def title = "ACMmr"
def fileName = "./${title}.txt"
def fileName2 = "./${title}2.txt"

def chan1 = Channel.one2one()
def chan2 = Channel.one2one()

def eDetails = new DataDetails( dName: wb.getName(),
                                dInitMethod: wb.init,
                                dCreateMethod: wb.create,
                                dInitData: [fileName, blockSize])

def eDetails2 = new DataDetails( dName: wb.getName(),
                                dInitMethod: wb.init,
                                dCreateMethod: wb.create,
                                dInitData: [fileName2, blockSize])

def network = []

network << new Emit( eDetails: eDetails,
                     output: chan1.out())

network << new Emit( eDetails: eDetails2,
                     output: chan2.out())

network  << new TestPoint( input: chan1.in(), id: "A" )

network  << new TestPoint( input: chan2.in(), id: "B" )

new PAR(network).run()
