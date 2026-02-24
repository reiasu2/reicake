package com.reiasu.reiparticlesapi.exceptions;

public final class RenderPipeInputException extends Exception {

    public RenderPipeInputException(int inputFBO, int inputChannel) {
        super("Render pipeline fbo: " + inputFBO + " color channel " + inputChannel + " can only have one input");
    }
}
