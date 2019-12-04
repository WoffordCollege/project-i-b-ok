project_root=$(dirname $(dirname $(dirname $(dirname $(dirname $(dirname $(dirname $0 )))))))
echo $project_root
rm -rf "$project_root/ethereum/node0/geth"
geth --datadir "$project_root/ethereum/node0" init "$project_root/ethereum/genesis.json"
geth --rpc --rpcapi personal,eth,net,web3,miner --mine --gasprice 0 --miner.etherbase 0x0fce4741f3f54fbffb97837b4ddaa8f769ba0f91 --nodiscover --rpccorsdomain "*" --datadir "$project_root/ethereum/node0" --miner.noverify --allow-insecure-unlock --unlock 0 --password "$project_root/ethereum/node0/keystore/password.txt"
