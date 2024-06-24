#version 150

uniform sampler2D DiffuseSampler;
uniform float Strength;
in vec2 texCoord;

out vec4 fragColor;

void main() {

    vec3 color =texture(DiffuseSampler,texCoord).xyz;

    fragColor = vec4(color + distance(vec2(0.5,0.5), texCoord)*vec3(171.0/256.0,2.0/256.0,2.0/256.0) , 1.0);
}