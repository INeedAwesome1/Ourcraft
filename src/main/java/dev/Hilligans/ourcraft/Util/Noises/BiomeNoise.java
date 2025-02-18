package dev.Hilligans.ourcraft.Util.Noises;

import dev.Hilligans.ourcraft.Biome.Biome;
import dev.Hilligans.ourcraft.Biome.Biomes;
import dev.Hilligans.ourcraft.GameInstance;

import java.util.Random;

public class BiomeNoise {

    double size = 200;
    public GameInstance gameInstance;

    KenPerlinNoise biome;
    //KenPerlinNoise humidity;

    PerlinNoise perlinNoise;
    PerlinNoise tempNoise;
    PerlinNoise humidityNoise;
    PerlinNoise randomNoise;
    PerlinNoise variationNoise;

    long noise;
    long temp;
    long humidity;
    long rand;
    long variation;


    public BiomeNoise(GameInstance gameInstance, Random random) {
        this.gameInstance = gameInstance;
        perlinNoise = new PerlinNoise(random.nextInt(),2,0.01,0.707,1);
        tempNoise = new PerlinNoise(random.nextInt(),2,0.01,0.707,1);
        humidityNoise = new PerlinNoise(random.nextInt(),2,0.01,0.707,1);
        randomNoise = new PerlinNoise(random.nextInt(),2,0.01,0.707,1);
        variationNoise = new PerlinNoise(random.nextInt(),2,0.01,0.707,1);

        noise = random.nextInt();
        temp = random.nextInt();
        humidity = random.nextInt();
        rand = random.nextInt();
        variation = random.nextInt();

        //biome = new KenPerlinNoise(random.nextInt());
        //humidity = new KenPerlinNoise(random.nextInt());

    }

    double max = 0;

    public double noise(int x, int z) {
        double noise = biome.noise(x / size,z / size);
        //double noise = perlinNoise.getHeight(x,z);
        if(noise > max) {
            System.out.println(noise);
            max = noise;
        }
        //noise += humidity.noise(x / size, z / size) + 1;
        return noise;
    }

    public Biome getBiome(int xx, int zz) {
        double x = xx / 4f + 0xFFFFFF;
        double z = zz / 4f + 0xFFFFFF;
        double noise = perlinNoise.getHeight(x,z) + 0.5;
        double temp = tempNoise.getHeight(x,z) + 0.5;
        double humidity = tempNoise.getHeight(x,z) + 0.5;
        double rand = randomNoise.getHeight(x,z) + 0.5;
        double variation = randomNoise.getHeight(x,z) + 0.5;

        //double noise = SimplexNoise.noise(x,z,this.noise);
     //   double temp = SimplexNoise.noise(x,z,this.temp);
      //  double humidity = SimplexNoise.noise(x,z,this.humidity);
       // double rand = SimplexNoise.noise(x,z,this.rand);
       // double variation = SimplexNoise.noise(x,z,this.variation);


        return Biomes.getBiome(gameInstance, noise,temp,humidity,rand,variation);
    }





}
