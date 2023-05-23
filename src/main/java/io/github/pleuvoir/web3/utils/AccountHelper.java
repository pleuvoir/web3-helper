package io.github.pleuvoir.web3.utils;

import io.github.pleuvoir.web3.config.AppConfig;
import java.math.RoundingMode;
import jnr.a64asm.PREF_ENUM;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.sql.SQLOutput;
import org.web3j.utils.Convert.Unit;

@Slf4j
public class AccountHelper {

    static Web3j web3j;


    public static Web3j getWeb3j() {
        if (web3j != null) {
            return web3j;
        }
        String apiKey = AppConfig.getInstance().getInfuraApiKey();
        log.info("init apiKey={}", apiKey);
        web3j = Web3j.build(new HttpService("https://mainnet.infura.io/v3/" + apiKey));
        return web3j;
    }


    @Data
    static class AccountDTO {

        private String privateKey;
        private String publicKey;
        private String address;
    }

    public static AccountDTO generateFullNewWalletFile(String filePath) {
        try {
            String wallet = WalletUtils.generateFullNewWalletFile("9527", new File(filePath));
            AccountDTO accountDTO = new AccountDTO();
            accountDTO.setPrivateKey(wallet.substring(0, 64));
            accountDTO.setPublicKey(wallet.substring(64, 128));
            accountDTO.setAddress(wallet.substring(128));
            return accountDTO;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 写入本地账户，返回钱包地址
     */
    public static String generateNewWalletFile(String directoryPath) {
        try {
            String walletFileName = WalletUtils.generateNewWalletFile(
                    "9527", // 设置一个密码用于加密私钥
                    new File(directoryPath), true); // 指定保存账户信息的目录

            String oldFileName = directoryPath + "/" + walletFileName;
            Credentials credentials = loadCredentials(oldFileName);
            String address = credentials.getAddress();

            File oldFile = new File(oldFileName);
            File newFile = new File(directoryPath + "/" + address);
            if (oldFile.renameTo(newFile)) {
                System.out.println("文件重命名成功。");
            } else {
                System.out.println("文件重命名失败。");
            }
            return address;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据keyStore加载账户
     */
    public static Credentials loadCredentials(String filePath) {
        try {
            return WalletUtils.loadCredentials("9527", new File(filePath));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }


    public static BigDecimal getBalance(String address) {
        try {
            log.info("获取以太坊账户余额 {}", address);
            EthGetBalance balance = getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            return new BigDecimal(balance.getBalance());
        } catch (IOException e) {
            log.error("", e);
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取以太坊的最新 GAS 费用
     */
    public static BigDecimal getGasPrice() {
        try {
            BigInteger gasPrice = getWeb3j().ethGasPrice().send().getGasPrice();
            return Convert.fromWei(String.valueOf(gasPrice), Unit.GWEI).setScale(0, RoundingMode.HALF_UP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException {
        BigDecimal gasPrice = getGasPrice();

        System.out.println(gasPrice);

        // 将 gas price 转换为 Gwei
        BigDecimal bigDecimal = Convert.fromWei(String.valueOf(gasPrice), Unit.GWEI).setScale(2, RoundingMode.HALF_UP);

        System.out.println(bigDecimal + " Gwei");

//        BigDecimal bigDecimal = Convert.toWei(gasPrice.toString(), Convert.Unit.GWEI);
//
//
//        System.out.println(bigDecimal);
//
//        EthGetBalance send = web3j.ethGetBalance("0x6f7a5b92f38961355c331ffe1dec145886f28b24", DefaultBlockParameterName.LATEST)
//                .send();
//        System.out.println(send.getBalance());
//
//        BigDecimal balance = getBalance("0x6f7a5b92f38961355c331ffe1dec145886f28b24");
//        System.out.println(balance);
    }
}
