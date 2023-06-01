#version 460 core

#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D Texture;
in vec2 Frag_UV;
in vec4 Frag_Color;

out vec4 color;
void main(){
    color = Frag_Color * texture2D(Texture, Frag_UV.st);
}