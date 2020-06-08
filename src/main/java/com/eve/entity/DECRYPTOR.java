package com.eve.entity;

public enum DECRYPTOR {
    Accelerant(20, 1, 2, 10),
    Attainment(80, 4, -1, 4),
    Augmentation(-40, 9, -1, -2),
    OptimizedAttainment(90, 2, 1, -2),
    OptimizedAugmentation(-10, 7, 2, 0),
    Parity(50, 3, 1, -2),
    Process(10, 0, 3, 6),
    Symmetry(0, 2, 1, 8)
    ;

    public int probabilityMultiplier;
    public int Runs;
    public int ME;
    public int TE;

    DECRYPTOR(int probabilityMultiplier, int runs, int ME, int TE) {
        this.probabilityMultiplier = probabilityMultiplier;
        Runs = runs;
        this.ME = ME;
        this.TE = TE;
    }
}
