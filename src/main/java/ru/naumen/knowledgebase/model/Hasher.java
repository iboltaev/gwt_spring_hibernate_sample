package ru.naumen.knowledgebase.model;

public class Hasher {
    // analogous to C++'s boost::hash_combine
    public static int hashCombine(int seed, int hash) {
        return seed ^ hash + 0x9e3779b9 + (seed << 6) + (seed >> 2);
    }
}
