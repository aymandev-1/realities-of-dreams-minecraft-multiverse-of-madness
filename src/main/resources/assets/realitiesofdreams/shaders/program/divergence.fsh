#version 150

// Scene 019: for a heartbeat the world is seen twice, two images slightly apart.
uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

out vec4 fragColor;

void main() {
    vec4 here = texture(DiffuseSampler, texCoord);
    vec4 echo = texture(DiffuseSampler, clamp(texCoord + vec2(oneTexel.x * 18.0, oneTexel.y * 4.0), 0.0, 1.0));
    vec3 c = mix(here.rgb, echo.rgb, 0.45);
    fragColor = vec4(c * vec3(0.93, 0.97, 1.08), 1.0);
}
