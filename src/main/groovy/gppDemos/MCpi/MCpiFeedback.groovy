package gppDemos.MCpi

import jcsp.lang.ChannelOutput

class MCpiFeedback extends gppLibrary.DataClass {
  static int previousIterations = 0
  static int previousWithin = 0
  static float errorMargin = 0.1
  static float previousPi = 0.0
  static boolean sentFeedback = false
  static final String initClass = "initClass"
  static final String feedbackBool = "feedbackBool"

  public int initClass(List p){
    errorMargin = p[0]
    return completedOK
  }

  public int feedbackBool (def obj, ChannelOutput out){
    if ( !sentFeedback) {
      previousIterations = previousIterations + obj.iterations
      previousWithin = previousWithin + obj.within
      def currentPi = 4.0 * ((float) previousWithin / (float) previousIterations)
      if ( Math.abs(currentPi - previousPi) < errorMargin){
        out.write(false)
        println " Feedback has pi = $currentPi with previous = $previousPi"
        sentFeedback = true
      }
      else
        previousPi = currentPi
    }
    return completedOK
  }
}
