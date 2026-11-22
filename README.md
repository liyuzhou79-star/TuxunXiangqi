# 将！

免费开源的中国象棋 App，基于 Pikafish 引擎。

## 简介

「将！」是一款完全免费、开源的象棋应用：

- **强力引擎**：内置 Pikafish（开源象棋引擎，GPL-3.0），全部计算在手机本地完成，不联网、不上传棋局
- **AI 一选推荐**：对局中给出引擎首选着法与候选变化（开发中）
- **形势判断**：局势评估一目了然（开发中）
- **干净清爽**：没有广告，没有账号，没有内购

> 项目刚刚起步：当前版本 0.1.0 是第一个里程碑，包含应用骨架、Material 3 界面雏形、可实际走棋的象棋棋盘，以及在 CI 中为 Android 编译的 Pikafish 引擎。引擎对弈功能正在接入中。

## 功能规划

- 人机对弈（多档难度）
- AI 一选推荐与候选着法
- 形势判断与复盘分析
- 棋谱保存、导入与导出
- 人人对弈与 AI 表演赛

## 构建

调试包由 GitHub Actions 的 Build Debug 工作流构建。Pikafish 引擎二进制在 CI 中使用 Android NDK 编译（arm64-v8a），NNUE 网络文件为 Pikafish 官方默认网络（pikafish.nnue）。

## 许可

本项目以 GPL-3.0 许可开源。所用引擎 [Pikafish](https://github.com/official-pikafish/Pikafish) 同为 GPL-3.0 许可，版权归 Pikafish 开发者所有。
