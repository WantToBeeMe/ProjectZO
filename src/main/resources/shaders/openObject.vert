#version 460 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec4 aColor;

uniform mat4 uProjection;
uniform mat4 uView;
uniform float uDepth;
uniform mat3 uTransform;

out vec4 fColor;
out vec2 fPos;

void main(){
    fColor = aColor;
    fPos = aPos.xy;
    // Apply 2D transformation to the vertex position
    vec3 transformedPos = vec3(uTransform * vec3(aPos.xy, 1.0));

    // Apply projection and view matrices
    gl_Position = uProjection * uView * vec4(transformedPos.xy, uDepth + aPos.z , 1.0);
}