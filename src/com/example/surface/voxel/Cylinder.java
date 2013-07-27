package com.example.surface.voxel;

import rajawali.BaseObject3D;

public class Cylinder extends BaseObject3D {
    public static final int DIV = 32;

    public Cylinder(float radius, float height) {
        int numVertices = (1 + DIV) + (DIV * 2) + (1 + DIV);
        int numIndices = (DIV + DIV * 2 + DIV) * 3;

        float[] vertices = new float[numVertices * 3];
        float[] normals = new float[numVertices * 3];
        float[] textureCoords = new float[numVertices * 2];
        float[] colors = new float[numVertices * 4];
        int[] indices = new int[numIndices];

        float top = height / 2;
        float bottom = - height / 2;

        vertices[0] = 0;
        vertices[1] = 0;
        vertices[2] = top;
        normals[0] = 0;
        normals[1] = 0;
        normals[2] = 1;

        vertices[(1 + DIV + DIV * 2) * 3 + 0] = 0;
        vertices[(1 + DIV + DIV * 2) * 3 + 1] = 0;
        vertices[(1 + DIV + DIV * 2) * 3 + 2] = bottom;
        normals[(1 + DIV + DIV * 2) * 3 + 0] = 0;
        normals[(1 + DIV + DIV * 2) * 3 + 1] = 0;
        normals[(1 + DIV + DIV * 2) * 3 + 2] = -1;

        for (int i = 0; i < DIV; i++) {
            float x = (float) Math.cos(Math.PI * 2 * i / DIV);
            float y = (float) Math.sin(Math.PI * 2 * i / DIV);

            // top
            vertices[(1 + i) * 3 + 0] = x * radius;
            vertices[(1 + i) * 3 + 1] = y * radius;
            vertices[(1 + i) * 3 + 2] = top;
            normals[(1 + i) * 3 + 0] = 0;
            normals[(1 + i) * 3 + 1] = 0;
            normals[(1 + i) * 3 + 2] = 1;
            indices[i * 3 + 0] = 0;
            indices[i * 3 + 1] = 1 + i;
            indices[i * 3 + 2] = 1 + (i + 1) % DIV;

            // side
            vertices[(1 + DIV + i) * 3 + 0] = x * radius;
            vertices[(1 + DIV + i) * 3 + 1] = y * radius;
            vertices[(1 + DIV + i) * 3 + 2] = top;
            normals[(1 + DIV + i) * 3 + 0] = x;
            normals[(1 + DIV + i) * 3 + 1] = y;
            normals[(1 + DIV + i) * 3 + 2] = 0;

            vertices[(1 + DIV * 2 + i) * 3 + 0] = x * radius;
            vertices[(1 + DIV * 2 + i) * 3 + 1] = y * radius;
            vertices[(1 + DIV * 2 + i) * 3 + 2] = bottom;
            normals[(1 + DIV * 2 + i) * 3 + 0] = x;
            normals[(1 + DIV * 2 + i) * 3 + 1] = y;
            normals[(1 + DIV * 2 + i) * 3 + 2] = 0;

            indices[(DIV + i) * 3 + 0] = 1 + DIV + i;
            indices[(DIV + i) * 3 + 1] = 1 + DIV * 2 + i;
            indices[(DIV + i) * 3 + 2] = 1 + DIV * 2 + (i + 1) % DIV;
            indices[(DIV + DIV + i) * 3 + 0] = 1 + DIV * 2 + (i + 1) % DIV;
            indices[(DIV + DIV + i) * 3 + 1] = 1 + DIV + (i + 1) % DIV;
            indices[(DIV + DIV + i) * 3 + 2] = 1 + DIV + i;

            // bottom
            vertices[(1 + DIV + DIV * 2 + 1 + i) * 3 + 0] = x * radius;
            vertices[(1 + DIV + DIV * 2 + 1 + i) * 3 + 1] = y * radius;
            vertices[(1 + DIV + DIV * 2 + 1 + i) * 3 + 2] = bottom;
            normals[(1 + DIV + DIV * 2 + 1 + i) * 3 + 0] = 0;
            normals[(1 + DIV + DIV * 2 + 1 + i) * 3 + 1] = 0;
            normals[(1 + DIV + DIV * 2 + 1 + i) * 3 + 2] = -1;
            indices[(DIV + DIV * 2 + i) * 3 + 0] = 1 + DIV + DIV * 2;
            indices[(DIV + DIV * 2 + i) * 3 + 1] = 1 + DIV + DIV * 2 + 1 + (i + 1) % DIV;
            indices[(DIV + DIV * 2 + i) * 3 + 2] = 1 + DIV + DIV * 2 + 1 + i;
        }

        for (int i = 0; i < numVertices; i++) {
            colors[i * 4 + 0] = 1.0f;
            colors[i * 4 + 1] = 1.0f;
            colors[i * 4 + 2] = 1.0f;
            colors[i * 4 + 3] = 1.0f;
        }

        setData(vertices, normals, textureCoords, colors, indices);
    }
}
