/**
 * This demonmstration implements a Map Reduce architecture to count the number of words in an input text file.
 * It utilises the Map Reduce processes provided in jcsp.gppLibrary.mapReduce, Mapper and Reducer.  The architecture also uses
 * a ThreePhaseWorker process as part of the processing.<p>
 *
 * Two versions of the source of each example are provided. The version ending 'Chans' has the network defined using
 * user defined channel definitions and each process declares its input and output channels.  The version
 * ending '_gpp' does not define the channels nor the channel declarations in the process definitions.  The processes
 * have to be defined in the order in which they occur in the dataflow through the application solution.  The processes are preceded
 * by the annotation //NETWORK and terminated by //END NETWORK.  The program {@link jcsp.gppLibrary.build.GPPbuild} can then be used
 * to create the channel definitions, channel declarations and the required parallel constructor required to invoke the process network.<p>
 *
 *<pre>
 * Author, Licence and Copyright statement
 * author  Jon Kerridge
 * 		   School of Computing
 * 		   Edinburgh Napier University
 * 		   Merchiston Campus,
 * 		   Colinton Road
 * 		   Edinburgh EH10 5DT
 *
 * Author contact: j.kerridge (at) napier.ac.uk
 *
 * Copyright  Jon Kerridge Edinburgh Napier University *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *</pre>
 */

package gppDemos.mapReduceV0;
