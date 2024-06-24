#version 150

uniform sampler2D DiffuseSampler;
uniform float GlowRadius;
uniform float Taper;
uniform vec2 ScreenSize;
uniform mat4x4 Hearts;
uniform float Size;

in vec2 texCoord;

out vec4 fragColor;

float getFromMat(int index) {
    return Hearts[index/4][index%4];
}
float dot2( in vec2 v ) { return dot(v,v); }
float heart(vec2 p, float s) { // https://www.shadertoy.com/user/belfry
    p /= s;
    vec2 q = p;
    q.x *= 0.5 + .5 * q.y;
    q.y -= abs(p.x) * .63;
    return (length(q) - .7) * s;
}


float glow(float dist, float radius, float intensity){
    return pow(radius/dist, intensity);
}

void main() {
    vec3 overlay = vec3(243./256.0, 176./256.0, 245./256.0);
    float a = 0.;
    vec2 p = (texCoord*ScreenSize*2.0-ScreenSize.xy)/ScreenSize.y;
    for (int i = 0; i < 5; i++) {
        float c = heart(p-vec2(ScreenSize.x/ScreenSize.y*(-1. + 2.*getFromMat(3*i)),0.5-2.*getFromMat(3*i+1)),getFromMat(3*i+2)*Size);
        a += glow(max(c,0.0), GlowRadius, Taper);
    } // I love shaders
    vec3 color = texture(DiffuseSampler, texCoord).xyz;
    fragColor = vec4((overlay*0.85*a)+color+0.2*vec3(243./256., 58./256., 107./256.), 1.0);
}