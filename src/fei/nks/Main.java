package fei.nks;

import java.io.*;
import java.security.SecureRandom;
import java.util.Random;

public class Main
{
    private static int getLastBite(int register32)
    {
        return register32 & 0x00000001;
    }

    private static int shiftRight(int register32)
    {
        return register32 >>> 1;
    }

    private static int xorFirstBite(int register32)
    {
        return register32 ^  0x80000000;
    }

    private static int computeXt(int register32, int[] poly32Indices)
    {
        int retValue = 0;
        for(int idx : poly32Indices)
        {
            int tmp = register32 >> idx;
            retValue ^= tmp & 1;
        }
        return retValue;
    }

    private static byte[] generateLFSR(int register, int[] poly32Indices)
    {
        int sizeInBites = 8_000_000;
        //int register= Integer.parseInt(seed, 16);
        System.out.println("Start:" + String.format("Register: %32s", Integer.toBinaryString(register)).replace(" ", "0"));
        byte[] randomBites = new byte[sizeInBites]; // TODO use better structure7
        int xt = 0;

        for(int i = 0; i < sizeInBites; i++)
        {
            // las bit is output
            randomBites[i] = (byte) getLastBite(register);
            xt = computeXt(register, poly32Indices);
            if(xt == 1)
            {
                // value f will be placed on first place of register. State contains others bites
                register = xorFirstBite(register);
            }
            // now value of f is valid in register
            register = shiftRight(register);
            //System.out.println("Iter:" + i + ", Register:" + String.format("Register:%32s", Integer.toBinaryString(register)).replace(" ", "0"));
        }
        return randomBites;
    }

    private static byte[] reduceBytesToBites(byte[] arr)
    {
        byte b = 0;
        byte[] toRet = new byte[arr.length / 8];
        for(int i = 0; i < arr.length; i+=8)
        {
            for(int j = i; j < i + 8; j++)
            {
                b <<= 1;
                b ^= arr[j];
            }
            toRet[i / 8] = b;
            b = 0;
        }
        return toRet;
    }

    public static void main(String[] args) throws IOException
    {
        int[] poly32Indices = new int[]{0, 3};
        String fileName = "resources/testnks";
        int maxValue = (int)Math.pow(2, 31) - 1;
        Random random = new SecureRandom();
        OutputStream outputStream = new FileOutputStream(fileName);

        for(int i = 0; i < 100; i++)
        {
            int rnd = Math.abs(random.nextInt() % maxValue);
            byte[] randomLFSR = generateLFSR(rnd, poly32Indices);
            byte[] reduced = reduceBytesToBites(randomLFSR);
            outputStream.write(reduced);
        }
        System.out.println("end");
    }
}
