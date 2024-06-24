#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;
uniform float Strength;
in vec2 texCoord;

out vec4 fragColor;
//https://github.com/Noryea/motionblur-fabric figure out liscne stuff
void main() {

    vec3 color = vec3(dot(texture(DiffuseSampler,texCoord).xyz, vec3(0.299, 0.587, 0.114)));
    vec3 color2 = texture(PrevSampler,texCoord).xyz;
    fragColor = vec4(mix(color, color2, 0.95),1.0);
}